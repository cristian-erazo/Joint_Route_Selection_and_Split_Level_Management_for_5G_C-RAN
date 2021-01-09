graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
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
    prc 5
    ant 8
    prb 6
    x 24
    y 53
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 7
    prb 6
    x 17
    y 57
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 9
    prb 6
    x 24
    y 44
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 10
    prb 3
    x 120
    y 34
  ]
  edge [
    source 0
    target 3
    bandwith 496
    delay 407
  ]
  edge [
    source 0
    target 5
    bandwith 679
    delay 280
  ]
  edge [
    source 1
    target 2
    bandwith 131
    delay 272
  ]
  edge [
    source 1
    target 4
    bandwith 567
    delay 233
  ]
]
