graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 4
  ]
  node [
    id 2
    label "2"
    type 1
    prc 3
    ant 4
    prb 5
    x 22
    y 117
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 6
    prb 4
    x 61
    y 68
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 10
    prb 2
    x 73
    y 110
  ]
  edge [
    source 0
    target 4
    bandwith 311
    delay 460
  ]
  edge [
    source 1
    target 2
    bandwith 634
    delay 172
  ]
  edge [
    source 1
    target 3
    bandwith 889
    delay 481
  ]
]
