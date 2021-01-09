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
    prc 2
  ]
  node [
    id 2
    label "2"
    type 1
    prc 1
    ant 3
    prb 4
    x 103
    y 98
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 10
    prb 6
    x 101
    y 19
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 4
    prb 2
    x 31
    y 109
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 3
    prb 3
    x 87
    y 74
  ]
  edge [
    source 0
    target 3
    bandwith 812
    delay 309
  ]
  edge [
    source 0
    target 5
    bandwith 133
    delay 210
  ]
  edge [
    source 1
    target 2
    bandwith 578
    delay 213
  ]
  edge [
    source 1
    target 4
    bandwith 839
    delay 449
  ]
]
