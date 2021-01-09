graph [
  node [
    id 0
    label "0"
    type 2
    prc 4
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
    ant 9
    prb 4
    x 62
    y 110
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 4
    prb 6
    x 38
    y 34
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 7
    prb 3
    x 27
    y 108
  ]
  node [
    id 5
    label "5"
    type 1
    prc 1
    ant 7
    prb 4
    x 73
    y 39
  ]
  edge [
    source 0
    target 4
    bandwith 900
    delay 490
  ]
  edge [
    source 0
    target 3
    bandwith 728
    delay 418
  ]
  edge [
    source 1
    target 2
    bandwith 313
    delay 269
  ]
  edge [
    source 1
    target 5
    bandwith 590
    delay 290
  ]
]
