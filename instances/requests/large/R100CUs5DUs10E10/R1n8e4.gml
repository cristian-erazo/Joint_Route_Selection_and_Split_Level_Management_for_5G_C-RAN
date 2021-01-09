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
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 2
    prc 3
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 3
    prb 4
    x 35
    y 75
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 9
    prb 4
    x 50
    y 64
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 7
    prb 6
    x 60
    y 115
  ]
  node [
    id 7
    label "7"
    type 1
    prc 5
    ant 3
    prb 2
    x 54
    y 92
  ]
  edge [
    source 0
    target 4
    bandwith 104
    delay 344
  ]
  edge [
    source 1
    target 5
    bandwith 921
    delay 162
  ]
  edge [
    source 2
    target 7
    bandwith 849
    delay 486
  ]
  edge [
    source 3
    target 6
    bandwith 798
    delay 483
  ]
]
